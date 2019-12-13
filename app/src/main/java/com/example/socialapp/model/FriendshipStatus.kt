package com.example.socialapp

enum class FriendshipStatus(val status: String) {
    INVITATION_SENT("Invite sent"),
    INVITATION_RECEIVED("Accept invite"),
    ACCEPTED("Friends")
}