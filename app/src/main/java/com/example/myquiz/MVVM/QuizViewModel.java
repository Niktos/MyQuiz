package com.example.myquiz.MVVM;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myquiz.Model.QuizModel;

import java.util.List;

public class QuizViewModel extends ViewModel implements FirebaseRepository.OnFireStoreDataAdded {

    MutableLiveData<List<QuizModel>> quizModelListData = new MutableLiveData<>();


    FirebaseRepository firebaseRepo = new FirebaseRepository(this);

    public QuizViewModel() {
        this.firebaseRepo.GetDataFromFirebase();
    }


    public LiveData<List<QuizModel>> getLiveDataFromFirestore() {

        return quizModelListData;
    }


    @Override
    public void quizDataAdded(List<QuizModel> quizModelList) {

        quizModelListData.setValue(quizModelList);

    }

    @Override
    public void OnError(Exception exception) {

    }
}
