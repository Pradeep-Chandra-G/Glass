"use client";
import React from "react";

interface QuestionsListProps {
  questionCount: number;
  questionIds: number[];
  answers: {
    [key: number]: { status: "unseen" | "seen" | "answered" | "review" };
  };
  onSelect: (questionId: number) => void;
}

function QuestionsList({
  questionCount,
  questionIds,
  answers,
  onSelect,
}: QuestionsListProps) {
  const getColor = (status: string) => {
    switch (status) {
      case "answered":
        return "bg-green-500";
      case "review":
        return "bg-dark-amethyst";
      case "seen":
        return "bg-amber-400";
      default:
        return "bg-white";
    }
  };

  return (
    <div className="w-full h-[40vh] mt-5 rounded-md bg-gray-200 overflow-y-auto no-scrollbar p-4">
      <div className="grid grid-cols-5 gap-4">
        {questionIds.slice(0, questionCount).map((id) => (
          <div
            key={id}
            onClick={() => onSelect(id)}
            className={`flex items-center justify-center w-12 h-12 rounded-full cursor-pointer hover:ring-2 transition-colors ${getColor(
              answers[id]?.status || "unseen"
            )}`}
          >
            {id}
          </div>
        ))}
      </div>
    </div>
  );
}

export default QuestionsList;
