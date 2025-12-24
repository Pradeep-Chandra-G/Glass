"use client";

import React, { useState, useMemo, useEffect } from "react";
import { useQuizTimer } from "../../components/context/QuizTimerContext";
import { useAuth } from "../../components/context/AuthContext";
import { useQuery, useMutation } from "@tanstack/react-query";
import QuizQuestion from "../ui/QuizQuestion";
import NavigationButtons from "../ui/NavigationButtons";
import StatusBar from "../ui/StatusBar";
import QuestionsList from "../ui/QuestionsList";
import { fetchQuizQuestions } from "../../api/quiz";
import { submitAnswer } from "@/app/api/submission";
import { QuizQuestionDTO } from "@/app/types/quiz";

interface AnswerRecord {
  [questionId: number]: {
    answer?: number[];
    status: "unseen" | "seen" | "answered" | "review";
  };
}

function QuizSection({ quizId }: { quizId: string }) {
  const { token } = useAuth();
  const [attemptId, setAttemptId] = useState<number | null>(null);
  const { expired, expire } = useQuizTimer();

  /* ------------------ START ATTEMPT ------------------ */
  useEffect(() => {
    const startAttempt = async () => {
      const res = await fetch(
        `http://localhost:8080/api/attempt/start/${quizId}`,
        {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      const data = await res.json();
      setAttemptId(data.id);
    };

    if (token && !attemptId) {
      startAttempt();
    }
  }, [token, quizId, attemptId]);

  /* ------------------ FETCH QUESTIONS ------------------ */
  const { data: questions = [] } = useQuery<QuizQuestionDTO[]>({
    queryKey: ["quiz-questions", quizId],
    queryFn: () => fetchQuizQuestions(quizId),
    staleTime: Infinity,
    gcTime: Infinity,
  });

  const questionIds = useMemo(() => questions.map((q) => q.id), [questions]);
  const TOTAL_QUESTIONS = questionIds.length;

  const [currentIndex, setCurrentIndex] = useState(0);
  const currentQuestionId = questionIds[currentIndex];

  const [answers, setAnswers] = useState<AnswerRecord>({});

  useEffect(() => {
    if (!questionIds.length || Object.keys(answers).length) return;

    const init: AnswerRecord = {};
    questionIds.forEach((id) => {
      init[id] = { status: "unseen" };
    });

    setAnswers(init);
  }, [questionIds]);

  /* ------------------ COMPLETE ATTEMPT ON TIMEOUT ------------------ */
  const completeMutation = useMutation({
    mutationFn: async () => {
      // Submit all pending answers
      await Promise.all(
        Object.entries(answers)
          .filter(([_, v]) => v.answer?.length)
          .map(([qid, v]) =>
            submitAnswer(attemptId!, Number(qid), v.answer!)
          )
      );

      // Complete the attempt
      await fetch(`http://localhost:8080/api/attempt/complete/${attemptId}`, {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
      });
    },
  });

  useEffect(() => {
    if (expired && attemptId) {
      completeMutation.mutate();
      // Redirect to results
      window.location.href = `/results/${attemptId}`;
    }
  }, [expired, attemptId]);

  /* ------------------ OPTION TOGGLE ------------------ */
  const toggleOption = (
    questionId: number,
    optionId: number,
    multiSelect: boolean
  ) => {
    setAnswers((prev) => {
      const prevEntry = prev[questionId] ?? { status: "unseen" };
      const existing = prevEntry.answer ?? [];

      const updated = multiSelect
        ? existing.includes(optionId)
          ? existing.filter((id) => id !== optionId)
          : [...existing, optionId]
        : [optionId];

      return {
        ...prev,
        [questionId]: {
          ...prevEntry,
          answer: updated,
          status: "seen",
        },
      };
    });
  };

  const nextQuestion = () =>
    setCurrentIndex((i) => Math.min(i + 1, TOTAL_QUESTIONS - 1));
  const prevQuestion = () => setCurrentIndex((i) => Math.max(i - 1, 0));

  const seen = Object.values(answers).filter((a) => a.status === "seen").length;
  const unseen = Object.values(answers).filter(
    (a) => a.status === "unseen"
  ).length;
  const answered = Object.values(answers).filter(
    (a) => a.status === "answered"
  ).length;
  const review = Object.values(answers).filter(
    (a) => a.status === "review"
  ).length;

  const handleSaveAndNext = async () => {
    if (answers[currentQuestionId]?.answer?.length && attemptId) {
      // Submit to backend
      await submitAnswer(
        attemptId,
        currentQuestionId,
        answers[currentQuestionId].answer!
      );

      setAnswers((prev) => ({
        ...prev,
        [currentQuestionId]: {
          ...prev[currentQuestionId],
          status: "answered",
        },
      }));
    }
    nextQuestion();
  };

  return (
    <div className="flex gap-8 mx-16 w-full text-black h-[95%]">
      <div className="flex flex-col w-[70%] bg-white rounded-md">
        {currentQuestionId && (
          <QuizQuestion
            question={questions[currentIndex]}
            selectedOptions={answers[currentQuestionId]?.answer ?? []}
            onSelect={(opt, multi) =>
              toggleOption(currentQuestionId, opt, multi)
            }
          />
        )}
      </div>

      <div className="w-[30%] bg-white p-6 rounded-md flex flex-col">
        <StatusBar
          answered={answered}
          unseen={unseen}
          seen={seen}
          review={review}
        />

        <QuestionsList
          questionCount={TOTAL_QUESTIONS}
          questionIds={questionIds}
          answers={answers}
          onSelect={(qid) => setCurrentIndex(questionIds.indexOf(qid))}
        />

        <NavigationButtons
          onPrev={prevQuestion}
          onClearAnswer={() =>
            setAnswers((prev) => ({
              ...prev,
              [currentQuestionId]: { status: "seen" },
            }))
          }
          onMarkForReview={() =>
            setAnswers((prev) => ({
              ...prev,
              [currentQuestionId]: {
                ...prev[currentQuestionId],
                status: "review",
              },
            }))
          }
          onSaveAndNext={handleSaveAndNext}
        />
      </div>
    </div>
  );
}

export default QuizSection;