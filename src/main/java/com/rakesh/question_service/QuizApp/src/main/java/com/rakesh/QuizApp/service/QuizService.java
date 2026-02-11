package com.rakesh.QuizApp.service;

import com.rakesh.QuizApp.dao.QuestionDao;
import com.rakesh.QuizApp.dao.QuizDao;
import com.rakesh.QuizApp.model.Question;
import com.rakesh.QuizApp.model.QuestionWrapper;
import com.rakesh.QuizApp.model.Quiz;
import com.rakesh.QuizApp.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizService {
    @Autowired
    private QuizDao quizDao;
    @Autowired
    private QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, int noOfQuestions, String title) {
        List<Question> questions = questionDao.findRandomQuestionsByCategory(category,noOfQuestions);
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);

        quizDao.save(quiz);
        return new ResponseEntity<>("Quiz Created...", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Quiz quiz = quizDao.findById(id).orElse(null);
        if(quiz==null)
        {
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NOT_FOUND);
        }
        List<Question> questions = quiz.getQuestions();
        List<QuestionWrapper> questionsForUser = new ArrayList<>();
        for(Question question:questions)
        {
            QuestionWrapper questionWrapper = new QuestionWrapper();
            questionWrapper.setId(question.getId());
            questionWrapper.setQuestionTitle(question.getQuestionTitle());
            questionWrapper.setOption1(question.getOption1());
            questionWrapper.setOption2(question.getOption2());
            questionWrapper.setOption3(question.getOption3());
            questionWrapper.setOption4(question.getOption4());
            questionsForUser.add(questionWrapper);
        }
        return new ResponseEntity<>(questionsForUser,HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        Quiz quiz = quizDao.findById(id).orElse(null);
        if(quiz==null)
        {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Question> questions = quiz.getQuestions();
        int score = 0;
        int itr = 0;
        for(Response response : responses)
        {
            if(response.getAnswer().equalsIgnoreCase(questions.get(itr).getRightAnswer()))
                score++;

            itr++;
        }
        return new ResponseEntity<>(score,HttpStatus.OK);
    }
}
