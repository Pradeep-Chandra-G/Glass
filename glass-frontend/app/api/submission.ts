// api/submission.ts
export async function submitAnswer(
  quizId: number,
  questionId: number,
  selectedOptionIds: number[]
) {
  const res = await fetch("http://localhost:8080/api/submission/submit", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      quizId,
      questionId,
      selectedOptionIds,
    }),
  });

  if (!res.ok) {
    throw new Error("Failed to submit answer");
  }

  return res.json();
}
