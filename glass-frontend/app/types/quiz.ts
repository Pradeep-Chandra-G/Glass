// types/quiz.ts
export interface Option {
  id: number;
  text: string;
}

export interface QuizQuestionDTO {
  id: number;
  text: string;
  multiSelect: boolean;
  options: Option[];
}
