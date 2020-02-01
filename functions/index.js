// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

const db = admin.firestore();

// Function that populates reference to the newly created post document
// over timelines of people that are friends with the publisher of the post 
exports.addPostToFriendsTimelines =
    functions.firestore.document('posts/{postId}')
        .onCreate((snap, context) => {
            const newPost = snap.data();
            const newPostId = context.params.postId;
            const newPostCreatedByUserUid = newPost.createdByUserUid;
            const newPostCreatedDate = newPost.dateCreated.toDate();

            // Fields of document that is about to be added to each user timeline collection
            const data = {
                createdByUserUid: `${newPostCreatedByUserUid}`,
                dateCreated: newPostCreatedDate
            };

            // let writeBatch = db.batch();

            // Friends collection of post publisher
            const friendsCollectionRef = db.collection(`users/${newPostCreatedByUserUid}/friends`);
            // Query for member friends of user
            const friendsQuery = friendsCollectionRef.where('status', '==', 'Friends'); 
            // Each document id represents a user uid of a accepted/confirmed friend
            friendsQuery.get()
                .then(snapshot => {
                    // Populate new post in every friend timeline
                    snapshot.forEach(friendDocument => {
                        // Reference to the new document that is about being created in a friend timeline collection
                        // with this document id being the same as an id of a new post
                        const newTimelineDocRef = db.doc(`users/${friendDocument.id}/timeline/${newPostId}`);

                        // writeBatch.set(newTimelineDocRef, data);
                        newTimelineDocRef.set(data);
                    });
                    // Add post to the post publisher own timeline
                    const publisherTimelineDocRef = db.doc(`users/${newPostCreatedByUserUid}/timeline/${newPostId}`);

                    // writeBatch.set(publisherTimelineDocRef, data);
                    publisherTimelineDocRef.set(data)
                    // return writeBatch.commit();
                })
                .catch(err => {
                    console.log(err);
                });
        })


// deletes references to the deleted post from user friends timelines
exports.deletePostFromFriendsTimelines =
    functions.firestore.document('posts/{postId}')
        .onDelete((snap, context) => {
            // Triggered after deleting the post
            const postCreatedByUserUid = snap.data().createdByUserUid;
            const friendsOfUserQuery = db.collection(`users/${postCreatedByUserUid}/friends`).where('status', '==', 'Friends');
            const postToDeleteId = context.params.postId;

            // let deleteBatch = db.batch();

            friendsOfUserQuery.get()
                .then(snapshot => {
                    snapshot.forEach(friendDocument => {
                        const userId = friendDocument.id;
                        const postToDeleteDocRef = db.doc(`users/${userId}/timeline/${postToDeleteId}`);

                        postToDeleteDocRef.delete();
                        // deleteBatch.delete(postToDeleteDocRef);
                    });

                    const publisherTimelineDocRef = db.doc(`users/${postCreatedByUserUid}/timeline/${postToDeleteId}`);
                    // deleteBatch.delete(publisherTimelineDocRef);

                    publisherTimelineDocRef.delete();
                    // return deleteBatch.commit();
                })
                .catch(err => {
                    console.log(`Failed to delete deleted post from timelines: ${err}`);
                });

        })


// After removing user from friends list
// delete all your posts references from his timeline collection
exports.deleteYourPostsFromDeletedFriendTimeline =
    functions.firestore.document('users/{userId}/friends/{friendId}')
        .onDelete((snap, context) => {
            // Triggered after removing user from friends list
            if (snap.data().status != 'Friends') {
                console.log('It was just deleted invitation');
                return null;
            }
            else {
                // let deleteBatch = db.batch();
                const friendId = context.params.friendId;
                const userId = context.params.userId;
                const userPostsQuery = db.collection(`posts`).where('createdByUserUid', '==', userId);

                return userPostsQuery.get()
                    .then(snap => {
                        if (snap.empty) {
                            console.log('Query for user posts empty, returning null');
                            return null;
                        }
                        else {
                            snap.forEach(docSnap => {
                                const docId = docSnap.id;
                                console.log(`Deleted Friend id: ${friendId} || docId: ${docId}`);
                                const docRef = db.doc(`users/${friendId}/timeline/${docId}`);
                                docRef.delete();
                                // deleteBatch.delete(drf);
                            });
                            // return deleteBatch.commit();
                        }

                    })
                    .catch(err => {
                        console.log(`Failed to remove posts references from deleted user timeline: ${err}`);
                    });
            }
        })


// triggered when user user adds somebody to friends - it populates his posts in this somebody timeline
exports.addYourPostsToAddedFriendTimeline =
    functions.firestore.document('users/{userId}/friends/{friendId}')
        .onUpdate((changeSnap, context) => {
            // Triggered after accepting user to friends list

            const friendId = context.params.friendId;

            // let batch = db.batch();
            const yourPostsQuery = db.collection(`posts`).where('createdByUserUid', '==', `${context.params.userId}`);
            //added return
            return yourPostsQuery.get()
                .then(yourPosts => {
                    if (yourPosts.empty) {
                        console.log('There were no posts of the user to populate');
                        return null;
                    }
                    else {
                        // let batch = db.batch();
                        yourPosts.forEach(postDoc => {
                            const date = postDoc.get('dateCreated');
                            const docRef = db.doc(`users/${friendId}/timeline/${postDoc.id}`);
                            const data = {
                                createdByUserUid: `${context.params.userId}`,
                                dateCreated: date
                            };
                            docRef.set(data);
                            // batch.set(docRef, data);

                        })
                        // return batch.commit();
                    }

                })
                .catch(err => {
                    console.log(`Failed to populate posts in new friend timeline: ${err}`);
                });

        })


// Populates invitation in anothers user friends collection
exports.inviteFriend =
    functions.firestore.document('users/{userId}/friends/{friendId}')
        .onCreate((snap, context) => {
            // triggered after inviting user to friends

            // Avoiding infinite loop
            if (snap.data().status != 'Invite sent') {
                console.log('inviteFriend doc.onCreate triggered avoided infinite loop cuz status!=`Invite sent`, yey');
                return null;
            }
            else {
                const newStatus = 'Accept invite';
                const idOfUserThatInvites = context.params.userId;
                const idOfUserBeingInvited = context.params.friendId;

                const data = {
                    status: newStatus
                };
                return db.doc(`users/${idOfUserBeingInvited}/friends/${idOfUserThatInvites}`).set(data)
                    .catch(err => {
                        console.log(`Error while populating invitation to friends: ${err}`);
                    });
            }
        })


// Populates Accepted friendship status in anothers user friends collection
exports.acceptFriendInvite =
    functions.firestore.document('users/{userId}/friends/{friendId}')
        .onUpdate((changeSnap, context) => {
            // triggered after accepting friend request

            // Validate update operation to avoid infinite loop
            if (changeSnap.after.data().status != 'Friends' && changeSnap.before.data().status != 'Accept invite') {
                return null;
            }
            else {
                const newStatus = 'Friends';
                const idOfUserThatAcceptedInvite = context.params.userId;
                const idOfUserToPopulateAcceptAt = context.params.friendId;

                const data = {
                    status: newStatus
                };
                const docRef = db.doc(`users/${idOfUserToPopulateAcceptAt}/friends/${idOfUserThatAcceptedInvite}`);
                return docRef.update(data)
                    .catch(err => {
                        console.log(`Error while populating accepted invite: ${err}`);
                    });
            }
        })


// Populates removal of any friendship status in another user friends collection 
exports.deleteFriend =
    functions.firestore.document('users/{userId}/friends/{friendId}')
        .onDelete((snap, context) => {
            // delete document in 'users/{friendId}/friends/{userId}'
            const idOfUserThatDeletedFriend = context.params.userId;
            const idOfUserThatWasDeleted = context.params.friendId; //hehe not anymore
            const docRef = db.doc(`users/${idOfUserThatWasDeleted}/friends/${idOfUserThatDeletedFriend}`);
            return docRef.delete()
                .catch(err => {
                    console.log(`Failed to populate delete of a friend document: ${err}`);
                });
        })

// Increments likes counter of post by 1 after post got liked
exports.incrementPostLikesNumber =
    functions.firestore.document('posts/{postId}/likes/{userWhoLikedThePost}')
        .onCreate((snap, context) => {
            // Triggered after user liked the post
            const postId = context.params.postId;
            const postDocRef = db.doc(`posts/${postId}`);
            return postDocRef.update({ likesNumber: admin.firestore.FieldValue.increment(1) })
                .catch(err => {
                    console.log(`Failed to increment likesNumber by 1: ${err}`);
                });
        })

// Decrements likes counter of post by 1 after post got unliked
exports.decrementPostLikesNumber =
    functions.firestore.document('posts/{postId}/likes/{userWhoUnlikedThePost}')
        .onDelete((snap, context) => {
            const postId = context.params.postId;
            const postDocRef = db.doc(`posts/${postId}`);
            return postDocRef.update({ likesNumber: admin.firestore.FieldValue.increment(-1) })
                .catch(err => {
                    console.log(`Failed to decrement likesNumber by 1: ${err}`);
                });
        })


