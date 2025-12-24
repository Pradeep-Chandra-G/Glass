"use client";
import React from "react";

interface NavigationButtonsProps {
  onMarkForReview: () => void;
  onClearAnswer: () => void;
  onPrev: () => void;
  onSaveAndNext: () => void;
}

export default function NavigationButtons({
  onMarkForReview,
  onClearAnswer,
  onPrev,
  onSaveAndNext,
}: NavigationButtonsProps) {
  return (
    <div className="rounded-md w-full h-[28vh] mt-6 bg-gray-200 text-lg text-white font-mono">
      <div className="grid grid-cols-2 gap-4 h-full p-4">
        <button
          className="w-full h-full bg-dark-amethyst rounded-md hover:cursor-pointer flex items-center justify-center"
          onClick={onMarkForReview}
        >
          Mark for review
        </button>
        <button
          className="w-full h-full bg-red-500 rounded-md hover:cursor-pointer flex items-center justify-center"
          onClick={onClearAnswer}
        >
          Clear answer
        </button>
        <button
          className="w-full h-full bg-deep-sea rounded-md hover:cursor-pointer flex items-center justify-center"
          onClick={onPrev}
        >
          Previous
        </button>
        <button
          className="w-full h-full bg-green-500 rounded-md hover:cursor-pointer flex items-center justify-center"
          onClick={onSaveAndNext}
        >
          Save and Next
        </button>
      </div>
    </div>
  );
}
