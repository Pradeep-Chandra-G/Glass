import Navbar from "@/app/components/blocks/Navbar";
import QuizSection from "@/app/components/blocks/QuizSection";

export default async function QuizPage({
  params,
}: {
  params: Promise<{ quiz_id: string }>;
}) {
  const { quiz_id } = await params; // ✅ REQUIRED in Next 16

  return (
    <div className="flex flex-col flex-1 w-full items-center">
      <Navbar />
      <div className="mt-6 flex flex-1 w-full">
        {/* ✅ passing a STRING (serializable) */}
        <QuizSection quizId={quiz_id} />
      </div>
    </div>
  );
}
