"use client";

import { useAuth } from "../components/context/AuthContext";
import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";

interface Quiz {
  id: number;
  title: string;
  durationSeconds: number;
  status: "DRAFT" | "PUBLISHED" | "ARCHIVED";
  publishType: "PUBLIC" | "RESTRICTED";
}

export default function Dashboard() {
  const { user, token, logout, isAdmin } = useAuth();
  const router = useRouter();

  const { data: quizzes = [] } = useQuery<Quiz[]>({
    queryKey: ["available-quizzes"],
    queryFn: async () => {
      const res = await fetch("http://localhost:8080/api/quiz/all", {
        headers: { Authorization: `Bearer ${token}` },
      });
      return res.json();
    },
    enabled: !!token,
  });

  const publishedQuizzes = quizzes.filter((q) => q.status === "PUBLISHED");

  if (!user) {
    router.push("/auth");
    return null;
  }

  return (
    <div className="min-h-screen bg-azure-mist p-8">
      <div className="max-w-6xl mx-auto">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-4xl font-bold text-black">
              Welcome, {user.firstName}!
            </h1>
            <p className="text-gray-600">{user.email}</p>
          </div>
          <div className="space-x-4">
            {isAdmin && (
              <button
                onClick={() => router.push("/admin")}
                className="bg-dark-amethyst text-white px-6 py-2 rounded hover:opacity-90"
              >
                Admin Panel
              </button>
            )}
            <button
              onClick={logout}
              className="bg-red-500 text-white px-6 py-2 rounded hover:opacity-90"
            >
              Logout
            </button>
          </div>
        </div>

        <div className="bg-white rounded-lg p-6 mb-8">
          <h2 className="text-2xl font-bold mb-4 text-black">
            Available Quizzes
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {publishedQuizzes.map((quiz) => (
              <div
                key={quiz.id}
                className="border rounded-lg p-4 hover:shadow-lg transition cursor-pointer"
                onClick={() => router.push(`/quiz/${quiz.id}`)}
              >
                <h3 className="text-xl font-bold mb-2 text-black">
                  {quiz.title}
                </h3>
                <p className="text-gray-600">
                  Duration: {Math.floor(quiz.durationSeconds / 60)} minutes
                </p>
                <button className="mt-4 bg-deep-sea text-white px-4 py-2 rounded w-full hover:opacity-90">
                  Start Quiz
                </button>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-white rounded-lg p-6">
          <h2 className="text-2xl font-bold mb-4 text-black">My Attempts</h2>
          <p className="text-gray-600">
            Your previous quiz attempts will appear here.
          </p>
        </div>
      </div>
    </div>
  );
}
