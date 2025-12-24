"use client";

import React, { createContext, useContext, useState } from "react";

interface TimerContextType {
  expired: boolean;
  expire: () => void;
}

const TimerContext = createContext<TimerContextType | null>(null);

export function QuizTimerProvider({ children }: { children: React.ReactNode }) {
  const [expired, setExpired] = useState(false);

  return (
    <TimerContext.Provider
      value={{
        expired,
        expire: () => setExpired(true),
      }}
    >
      {children}
    </TimerContext.Provider>
  );
}

export function useQuizTimer() {
  const ctx = useContext(TimerContext);
  if (!ctx) throw new Error("useQuizTimer must be used inside provider");
  return ctx;
}
