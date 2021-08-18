package com.example.myquiz.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myquiz.MVVM.QuizViewModel;
import com.example.myquiz.Model.QuizModel;
import com.example.myquiz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;


public class DetailFragment extends Fragment implements View.OnClickListener {


    TextView quizTitle, desc, level, question, lastscoretext;
    ImageView imageView;
    Button takeQuiz;
    NavController navController;
    int position = 0;
    QuizViewModel viewModel;
    String quizId;
    String currentUserId;
    String quizName;
    long totalQuestions = 0L;


    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.detailfrag, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        quizTitle = view.findViewById(R.id.detailquiztitle);
        desc = view.findViewById(R.id.detailquizdesc);
        level = view.findViewById(R.id.detailleveltext);
        question = view.findViewById(R.id.detailquestionstext);
        navController = Navigation.findNavController(view);
        position = DetailFragmentArgs.fromBundle(getArguments()).getPosition();
        takeQuiz = view.findViewById(R.id.detailtakequizbutton);
        imageView = view.findViewById(R.id.detailimage);
        lastscoretext = view.findViewById(R.id.detaillastscoretext);


        takeQuiz.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = new ViewModelProvider(getActivity()).get(QuizViewModel.class);
        viewModel.getLiveDataFromFirestore().observe(getViewLifecycleOwner(), new Observer<List<QuizModel>>() {
            @Override
            public void onChanged(List<QuizModel> quizModelList) {
                quizTitle.setText(quizModelList.get(position).getQuizname());
                desc.setText(quizModelList.get(position).getDesc());
                question.setText(quizModelList.get(position).getQuestion() + "");
                level.setText(quizModelList.get(position).getLevel());


                Glide.with(getContext()).load(quizModelList.get(position).getImage())
                        .placeholder(R.drawable.placeholder_image)
                        .centerCrop().into(imageView);

                quizId = quizModelList.get(position).getQuizid();
                quizName = quizModelList.get(position).getQuizname();
                totalQuestions = quizModelList.get(position).getQuestion();

                LoadRecentResult();
            }
        });
    }

    private void LoadRecentResult() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {

            currentUserId = firebaseUser.getUid();

        }

        firebaseFirestore.collection("QuizList")
                .document(quizId).collection("Results").document(currentUserId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    DocumentSnapshot snapshot = task.getResult();

                    if (snapshot.exists() && snapshot != null) {

                        Long answerofcorrect = snapshot.getLong("correct");
                        Long inccorectanswer = snapshot.getLong("wrong");
                        Long missedquestions = snapshot.getLong("missed");


                        Long total = answerofcorrect + inccorectanswer + missedquestions;
                        Long percent = (answerofcorrect * 100) / total;

                        lastscoretext.setText(percent.toString() + "%");

                    }


                } else {

                    lastscoretext.setText(task.getException().toString());

                }


            }
        });


    }

    @Override
    public void onClick(View v) {

        DetailFragmentDirections.ActionDetailFragmentToQuizFragment
                action = DetailFragmentDirections.actionDetailFragmentToQuizFragment();
        action.setQuizid(quizId); //удалить и работает
        action.setQuizname(quizName);
        action.setTotalNumberOfQuestion(totalQuestions);
        navController.navigate(action);

    }
}