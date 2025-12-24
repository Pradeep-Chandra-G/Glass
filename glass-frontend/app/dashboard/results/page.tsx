"use client";

import { useQuery } from "@tanstack/react-query";
import { useAuth } from "@/app/components/context/AuthContext";
import { useRouter } from "next/navigation";
import { use } from "react";

interface Submission {
  id: number;
  question: {
    id: number;
    text: string;
    options: Array<{
      id: number;
      text: string;
      correct: boolean;
    }>;
  };
  selectedOptionIds: number[];
  correct: boolean;
}

interface QuizAttempt {
  id: number;
  score: number;
  correctAnswers: number;
  totalQuestions: number;
  startedAt: string;
  completedAt: string;
  status: string;
  quiz: {
    id: number;
    title: string;
    showSolutions: boolean;
  };
}

export default function ResultsPage({
  params,
}: {
  params: Promise<{ attempt_id: string }>;
}) {
  const { attempt_id } = use(params);
  const { token } = useAuth();
  const router = useRouter();

  const { data: attempt } = useQuery<QuizAttempt>({
    queryKey: ["attempt", attempt_id],
    queryFn: async () => {
      const res = await fetch(
        `http://localhost:8080/api/attempt/${attempt_id}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      return res.json();
    },
    enabled: !!token,
  });

  const { data: submissions = [] } = useQuery<Submission[]>({
    queryKey: ["submissions", attempt_id],
    queryFn: async () => {
      const res = await fetch(
        `http://localhost:8080/api/submission/attempt/${attempt_id}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      return res.json();
    },
    enabled: !!token,
  });

  if (!attempt) return <div>Loading...</div>;

  const percentage = Math.round(
    (attempt.correctAnswers / attempt.totalQuestions) * 100
  );

  return (
    <div className="min-h-screen bg-azure-mist p-8">
      <div className="max-w-4xl mx-auto">
        <div className="bg-white rounded-lg p-8 mb-8">
          <h1 className="text-4xl font-bold mb-4 text-black">
            Quiz Results: {attempt.quiz.title}
          </h1>

          <div className="grid grid-cols-3 gap-6 mb-6">
            <div className="bg-green-100 p-6 rounded-lg text-center">
              <div className="text-4xl font-bold text-green-700">
                {percentage}%
              </div>
              <div className="text-gray-600">Score</div>
            </div>

            <div className="bg-blue-100 p-6 rounded-lg text-center">
              <div className="text-4xl font-bold text-blue-700">
                {attempt.correctAnswers}/{attempt.totalQuestions}
              </div>
              <div className="text-gray-600">Correct Answers</div>
            </div>

            <div className="bg-purple-100 p-6 rounded-lg text-center">
              <div className="text-2xl font-bold text-purple-700">
                {attempt.status}
              </div>
              <div className="text-gray-600">Status</div>
            </div>
          </div>

          <button
            onClick={() => router.push("/dashboard")}
            className="bg-deep-sea text-white px-6 py-3 rounded hover:opacity-90"
          >
            Back to Dashboard
          </button>
        </div>

        {attempt.quiz.showSolutions && (
          <div className="bg-white rounded-lg p-8">
            <h2 className="text-2xl font-bold mb-6 text-black">
              Detailed Solutions
            </h2>

            {submissions.map((sub, idx) => (
              <div
                key={sub.id}
                className={`mb-6 p-6 rounded-lg ${
                  sub.correct ? "bg-green-50" : "bg-red-50"
                }`}
              >
                <div className="flex items-center mb-3">
                  <span
                    className={`text-2xl mr-3 ${
                      sub.correct ? "text-green-600" : "text-red-600"
                    }`}
                  >
                    {sub.correct ? "✓" : "✗"}
                  </span>
                  <h3 className="font-bold text-lg text-black">
                    Question {idx + 1}
                  </h3>
                </div>

                <p className="text-gray-800 mb-4">{sub.question.text}</p>

                <div className="space-y-2">
                  {sub.question.options.map((opt) => {
                    const isSelected = sub.selectedOptionIds.includes(opt.id);
                    const isCorrect = opt.correct;

                    return (
                      <div
                        key={opt.id}
                        className={`p-3 rounded ${
                          isCorrect
                            ? "bg-green-200 font-bold"
                            : isSelected
                            ? "bg-red-200"
                            : "bg-gray-100"
                        }`}
                      >
                        {opt.text}
                        {isCorrect && " ✓ (Correct)"}
                        {isSelected && !isCorrect && " (Your Answer)"}
                      </div>
                    );
                  })}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
