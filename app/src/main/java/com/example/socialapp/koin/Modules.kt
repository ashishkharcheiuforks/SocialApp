package com.example.socialapp.koin

import com.example.socialapp.repository.AlgoliaRepository
import com.example.socialapp.repository.FirestoreRepository
import com.example.socialapp.screens.adverts.AdvertsViewModel
import com.example.socialapp.screens.chat.ChatRoomsViewModel
import com.example.socialapp.screens.comments.CommentsViewModel
import com.example.socialapp.screens.conversation.ConversationViewModel
import com.example.socialapp.screens.createpost.CreatePostViewModel
import com.example.socialapp.screens.editprofile.EditProfileViewModel
import com.example.socialapp.screens.home.HomeViewModel
import com.example.socialapp.screens.invites.InvitesViewModel
import com.example.socialapp.screens.login.LoginViewModel
import com.example.socialapp.screens.register.RegisterViewModel
import com.example.socialapp.screens.userprofile.UserProfileViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val repoModule = module {
    single { AlgoliaRepository() }
    single { FirestoreRepository(get()) }
}

@ExperimentalCoroutinesApi
val viewmodelsModule = module {

    //Login
    viewModel { LoginViewModel(get()) }
    //Register
    viewModel { RegisterViewModel(get()) }
    //Invites
    viewModel { InvitesViewModel(get()) }
    //Home Page
    viewModel { HomeViewModel(get()) }
    //Create post
    viewModel { CreatePostViewModel(get()) }
    //Comments
    viewModel { (postId: String) -> CommentsViewModel(get(), postId) }
    //Adverts
    viewModel { AdvertsViewModel(get()) }
    //User Profile
    viewModel { (userId: String) -> UserProfileViewModel(get(), userId) }
    //Edit Profile
    viewModel { EditProfileViewModel(get()) }
    //Chat Rooms
    viewModel { ChatRoomsViewModel() }
    //Conversation
    viewModel { (userId: String) -> ConversationViewModel(get(), userId) }

}
