"use client";

import { QuizQuestionDTO } from "../../types/quiz";

interface Props {
  question: QuizQuestionDTO;
  selectedOptions: number[];
  onSelect: (optionId: number, multiSelect: boolean) => void;
}

export default function QuizQuestion({
  question,
  selectedOptions,
  onSelect,
}: Props) {
  return (
    <div className="flex flex-col flex-1 p-8">
      <h1 className="text-xl font-bold">
        Question {question.id}
      </h1>

      <p className="bg-gray-200 p-6 mt-6 rounded-md overflow-y-auto h-[40vh]">
        {question.text}
      </p>

      <div className="mt-6">
        <h2 className="font-bold text-xl mb-4">Options</h2>

        <div className="grid grid-cols-2 gap-4">
          {question.options.map((opt) => {
            const selected = selectedOptions.includes(opt.id);

            return (
              <button
                key={opt.id}
                onClick={() => onSelect(opt.id, question.multiSelect)}
                className={`p-4 rounded-md border transition ${
                  selected
                    ? "bg-amber-400 border-amber-600"
                    : "bg-white hover:bg-gray-100"
                }`}
              >
                {opt.text}
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
}
