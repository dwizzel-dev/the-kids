/*

$ npm install -g firebase-tools

Initiate your project:
$ firebase init

Deploy your functions:
$ firebase deploy

$ git init
$ git config --global user.email "dwizzel.dev@gmail.com"
$ git config --global user.name "dwizzel-dev"
$ git pull https://github.com/dwizzel-dev/the-kids-cloud-functions.git
$ git status
$ git add .
$ git status
$ git commit -m "TK.1"
$ git remote add -f -t master -m master origin https://github.com/dwizzel-dev/the-kids-cloud-functions.git
$ git push --set-upstream origin master

Examples:
https://github.com/MahmoudAlyuDeen/FirebaseIM
https://developers.google.com/cloud-messaging/concept-options

TODO: il faut un triger dans la DB pour faire le cleanup de ceux qui ne ce sont pas deconnecte

*/

//type de notification
const DEFAULT_LOCALE = "en";
const TYPE_NOTIF_INVITATION = 101;

const lang = {
    en: {
        title: "Invitation accepted",
        message: "The invitation sent to {PHONE} was accepted."
        }
    };

//function globale
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const db = admin.firestore();

//function locale
function getRandomIntInclusive(min, max) {
	min = Math.ceil(min);
	max = Math.floor(max);
	return Math.floor(Math.random() * (max - min + 1)) + min; 
}

function createUniqueActivationCode(event){
    //genere le code
    const randomCode = getRandomIntInclusive(10000,99999);
    let unique = false;
    //check si le unique code existe
}

function sendNotification(type, args){
    switch(type){
        case TYPE_NOTIF_INVITATION:
            //ca nous prend le token de celui qui a envoye la demande
            //et qui doit recevoir une confirmation lors de l'activation de sa demande
            const uid = args[0];
            const phoneNum = args[1];
            return db.collection("users").doc(uid)
                .get()
                .then((doc) => {
                    console.log("get recevier infos: " + uid);
                    const data = doc.data();
                    return {
                        token: data.token,
                        locale: data.locale
                        };
                })
                .then((result) => {
                    console.log("get token: " + result.token);
                    let locale = result.locale;
                    if(typeof(lang[locale]) != "object"){
                        locale = DEFAULT_LOCALE;
                    }
                    const strTitle = lang[locale].title;
                    const strMessage = lang[locale].message.replace("{PHONE}", phoneNum);
                    return admin.messaging().sendToDevice(result.token, {
                        notification: {
                            title: strTitle,
                            body: strMessage
                            //icon: sender.photoURL
                        }
                    });
                })
                .then((response) => {
                    console.log("Success Messenging:", response);
                    return true;
                })
                .catch((error) => {
                    console.log("Error Messenging:", error);
                    return false;
                });
            break;
        default:
            break;
    }
    return true;
}

exports.createInvitation = functions.firestore
	.document('invites/{inviteId}')
	.onCreate((event) => {
		//on va generer un code
		const randomCode = getRandomIntInclusive(10000,99999);
		const inviteId = event.params.inviteId;
		//on va mettre une nouvelle collection ce qui va eviter de trigger activateInvitation
		//car avec firestore on ne peut pas mettre de trigger sur un single field
		//on va le mettre dans la collection invites
		return db.collection('invites').doc(inviteId)
			.update({
				code: randomCode
			})
			.then(() => {
				console.log("code: " + inviteId + " --> " + randomCode);
				return randomCode;
            })
            .catch((err) => {
                console.log(err);
            });
	});

exports.activateInvitation = functions.firestore
	.document('invites/{inviteId}/state/{userId}')
	.onCreate((event) => {
        //le userID de celui qui accepte l'invitation
        const userA = event.params.userId; //2UT3SOpMxPOfAPrTFUwWTswAA0l1
        if(userA == null || userA == ""){
            return false;
        }
        //le userID de celui qui a envoye l'invitation
        //le inviteId
        const inviteId = event.params.inviteId; //PC6L4STvnhjqpI3Y94az
        const userB = event.data.data().from; //0CDFrsffJKbmVnU2St3NIQd0yOe2
        if(userB == null || userB == "" || inviteId == null || inviteId == ""){
            return false;
        }
        //sinon
        //on va chercher les infos de l'invitation
        const invitesRef = db.collection('invites').doc(inviteId); 
        const userRefA = db.collection('users').doc(userA);
        const userRefB = db.collection('users').doc(userB);
        let phoneNumSentTo = "";
        //get l'invitations
        return userRefB.collection('invitations').doc(inviteId)
            .get()
            .then((doc) => {
                let data = doc.data();
                console.log("get invitation: " + inviteId);
                phoneNumSentTo = data.phone;
                return {
                    name: data.name,
                    email: data.email,
                    phone: data.phone,
                    uid: userA,
                    gps: false,
                    status: 0,
                    position: new admin.firestore.GeoPoint(0.0, 0.0),
                    updateTime: admin.firestore.FieldValue.serverTimestamp()
                };
            })
            .then((result) => {
                console.log("set watchers: " + userB);
                //on va setter le watchers du user A
                return userRefB.collection('watchers').doc(userA).set(result);
            })
            .then(() =>{
                console.log("get infos: " + userA);
                //on va chercher les infos rentrer par le userA dans invites
                return invitesRef.collection('infos').doc(userA).get()
            })
            .then((doc) => {
                return doc.data();
            })
            .then((result) => {
                console.log("set watchings: " + userA);
                //on va setter le watching du user B
                return userRefA.collection('watchings').doc(userB).set({
                    name: result.name,
                    email: result.email,
                    phone: result.phone,
                    uid: userB,
                    gps: false,
                    status: 0,
                    position: new admin.firestore.GeoPoint(0.0, 0.0),
                    updateTime: admin.firestore.FieldValue.serverTimestamp()
                });
            })
            .then(() => {
                console.log("delete users invitation: " + inviteId);
                //on peut supprimer le invitation maintenant
                return userRefB.collection('invitations').doc(inviteId).delete();
            })
            .then(() => {
                console.log("delete invites[infos]: " + inviteId);
                //on peut supprimer le invite et les subcollections
                return invitesRef.collection('infos').doc(userA).delete()
            })
            .then(() => {
                console.log("delete invites[state]: " + inviteId);
                return invitesRef.collection('state').doc(userA).delete();
            })
            .then(() => {
                console.log("delete invites: " + inviteId);
                return invitesRef.delete();    
            })
            .then(() => {
                //send the notification comme quoi il est accepte dans les watchers
                //ca nous prend le token de l'usager a qui on envoe et le numero de code
                //la langue locale de l'usager aussi idealement (pour plus tard)
                return sendNotification(TYPE_NOTIF_INVITATION, [userB, phoneNumSentTo]);
            })
            .catch((err) => {
                console.log(err);
            });
	});

