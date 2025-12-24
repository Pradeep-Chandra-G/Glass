"use client";

import React, { useState, useMemo, useEffect } from "react";
import { useQuizTimer } from "../../components/context/QuizTimerContext";
import { submitAnswer } from "@/app/api/submission";

import { useQuery } from "@tanstack/react-query";
import QuizQuestion from "../ui/QuizQuestion";
import NavigationButtons from "../ui/NavigationButtons";
import StatusBar from "../ui/StatusBar";
import QuestionsList from "../ui/QuestionsList";
import { fetchQuizQuestions } from "../../api/quiz";
import { QuizQuestionDTO } from "@/app/types/quiz";

interface AnswerRecord {
  [questionId: number]: {
    answer?: number[];
    status: "unseen" | "seen" | "answered" | "review";
  };
}

function QuizSection({ quizId }: { quizId: string }) {
  /* ------------------ FETCH ALL QUESTIONS ONCE ------------------ */
  const { data: questions = [] } = useQuery<QuizQuestionDTO[]>({
    queryKey: ["quiz-questions", quizId],
    queryFn: () => fetchQuizQuestions(quizId),
    staleTime: Infinity,
    gcTime: Infinity,
  });

  /* ------------------ TIMER (AUTO SUBMIT) ------------------ */
  const { expired } = useQuizTimer();

  /* ------------------ QUESTION IDS ------------------ */
  const questionIds = useMemo(() => questions.map((q) => q.id), [questions]);

  const TOTAL_QUESTIONS = questionIds.length;

  /* ------------------ CURRENT QUESTION ------------------ */
  const [currentIndex, setCurrentIndex] = useState(0);
  const currentQuestionId = questionIds[currentIndex];

  /* ------------------ ANSWERS (INIT AFTER QUESTIONS LOAD) ------------------ */
  const [answers, setAnswers] = useState<AnswerRecord>({});

  useEffect(() => {
    if (!questionIds.length) return;
    if (Object.keys(answers).length) return; // prevent re-init

    const init: AnswerRecord = {};
    questionIds.forEach((id) => {
      init[id] = { status: "unseen" };
    });

    setAnswers(init);
  }, [questionIds]);

  /* ------------------ AUTO SUBMIT ON TIMEOUT ------------------ */
  useEffect(() => {
    if (!expired) return;

    const snapshot = answers; // freeze state

    const submitAll = async () => {
      await Promise.all(
        Object.entries(snapshot)
          .filter(([_, v]) => v.answer?.length)
          .map(([qid, v]) =>
            submitAnswer(Number(quizId), Number(qid), v.answer!)
          )
      );

      console.log("AUTO SUBMITTED");
      // redirect / show submitted screen here
    };

    submitAll();
  }, [expired]);

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
          ...prevEntry, // ✅ preserve everything
          answer: updated,
          status: "seen",
        },
      };
    });
  };

  /* ------------------ NAVIGATION ------------------ */
  const nextQuestion = () =>
    setCurrentIndex((i) => Math.min(i + 1, TOTAL_QUESTIONS - 1));

  const prevQuestion = () => setCurrentIndex((i) => Math.max(i - 1, 0));

  /* ------------------ STATUS COUNTS ------------------ */
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

  /* ------------------ RENDER ------------------ */
  return (
    <div className="flex gap-8 mx-16 w-full text-black h-[95%]">
      {/* LEFT */}
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

      {/* RIGHT */}
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
          onSaveAndNext={() => {
            if (answers[currentQuestionId]?.answer?.length) {
              setAnswers((prev) => ({
                ...prev,
                [currentQuestionId]: {
                  ...prev[currentQuestionId],
                  status: "answered",
                },
              }));
            }
            nextQuestion();
          }}
        />
      </div>
    </div>
  );
}

export default QuizSection;
