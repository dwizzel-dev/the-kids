
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });


/*

$ npm install -g firebase-tools

Initiate your project:
$ firebase init

Deploy your functions:
$ firebase deploy

https://github.com/firebase/functions-samples/blob/master/child-count/functions/index.js
https://github.com/firebase/functions-samples/blob/master/spotify-auth/functions/index.js
https://stackoverflow.com/questions/46576393/firebase-targeting-specific-firestore-document-field-with-cloud-functions
https://github.com/firebase/quickstart-js/blob/master/firestore/scripts/FriendlyEats.Data.js
https://cloud.google.com/nodejs/docs/reference/firestore/0.8.x/DocumentReference


GOOD:
https://javebratt.com/firebase-cloud-functions/
https://progblog.io/Create-a-URL-Shortener-with-Cloud-Firestore-and-Cloud-Functions/


INVITE STATES:

	public static final int ACCEPTED = 800;
    public static final int PENDING = 802;
    public static final int INNACTIVE = 803;


this function trigger on invites table 

1. the (A)user accept the invitation and enter the inviteId then activate it via his mobile phone

2. we update the "to:" with the (A)userId

3. we take the "from:" from invites and convert the (B)users->invitation to a (B)users->watchers keeping all the infos from the invitation and set the key to A(userId)

4. we then delete the (B)users->invitation

5. we take the "from:" from invites and insert it in (A)users->watching with all the other infos that (A)user provided via his mobile ans set the key to B(userId)

6. then we send notifications to (A)user and (B)user

7. server change the "state:" of invites to ACCEPTED, it was in PENDING state before

*/


const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const db = admin.firestore();

function getRandomIntInclusive(min, max) {
	min = Math.ceil(min);
	max = Math.floor(max);
	return Math.floor(Math.random() * (max - min + 1)) + min; 
  }

exports.createInvitation = functions.firestore
	.document('invites/{inviteId}')
	.onCreate((event) => {	
		//on va generer un code
		const randomCode = getRandomIntInclusive(10000,99999);
		const inviteId = event.params.inviteId;
		//on va le mettre dans la collection invites
		return db.collection('invites').doc(inviteId)
			.update({
				code:randomCode
			})
			.then(() => {
				console.log("code: " + inviteId + " --> " + randomCode);
				return;
			})
	});

exports.activateInvitation = functions.firestore
	.document('invites/{inviteId}')
	.onUpdate((event) => {
		//constant
		const ACCEPTED = 800;
		const PENDING = 802;
		const INNACTIVE = 803;
		const REMOVE = 804;
		//get the state
		const state = event.data.data().state;
		if(state == ACCEPTED){
			//le userID de celui qui accepte l'invitation
			const userA = event.data.data().to; //2UT3SOpMxPOfAPrTFUwWTswAA0l1
			//le userID de celui qui a envoye l'invitation
			const userB = event.data.data().from; //0CDFrsffJKbmVnU2St3NIQd0yOe2
			const defaultName = "Code_" + event.data.data().code;
			//le inviteId
			const inviteId = event.params.inviteId; //PC6L4STvnhjqpI3Y94az
			//on va chercher les infos de l'invitation
			// DB:/users/0CDFrsffJKbmVnU2St3NIQd0yOe2/invitations/PC6L4STvnhjqpI3Y94az
			// DB:/users/{userB}/invitations/{inviteId} [inviteId, name, phone, email, state]
			const userRefA = db.collection('users').doc(userA);
			const userRefB = db.collection('users').doc(userB);
			//get l'invitations
			return userRefB.collection('invitations').doc(inviteId)
				.get()
				.then((doc) => {
					const data = doc.data();
					console.log("get invitation: " + inviteId);
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
					console.log("set watchers: " + userA);
					//on va setter le watchers du user B
					return userRefB.collection('watchers').doc(userA).set(result);
				})
				.then(() => {
					console.log("set watchings: " + userB);
					//on va setter le watching du user A
					return userRefA.collection('watchings').doc(userB).set({
						name: defaultName,
						email: "",
						phone: "",
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
				.then(() =>{
					console.log("delete invites: " + inviteId);
					//on peut supprimer le invite
					return event.data.ref.delete();
				});
		}
	});

