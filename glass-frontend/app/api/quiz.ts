// api/quiz.ts
export async function fetchQuizQuestion(quizId: string, questionId: number) {
  const res = await fetch(`http://localhost:8080/api/question/${questionId}`, {
    credentials: "include",
  });

  if (!res.ok) {
    throw new Error("Failed to fetch question");
  }

  return res.json();
}

export async function fetchQuizQuestions(quizId: string) {
  const res = await fetch(`http://localhost:8080/api/question/quiz/${quizId}`, {
    credentials: "include",
  });
  if (!res.ok) throw new Error("Failed to fetch questions");
  return res.json();
}
