package com.example.socialapp.model

enum class FriendshipStatus(val status: String) {
    INVITATION_SENT("Invite sent"),
    INVITATION_RECEIVED("Accept invite"),
    ACCEPTED("Friends"),
    NO_STATUS("Invite")
}