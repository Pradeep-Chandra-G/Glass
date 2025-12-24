"use client";

import { useEffect, useState } from "react";
import { useQuizTimer } from "../../components/context/QuizTimerContext";

function formatSeconds(totalSeconds: number) {
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;

  return [hours, minutes, seconds]
    .map((v) => String(v).padStart(2, "0"))
    .join(":");
}

export default function ServerTime() {
  const [time, setTime] = useState("00:00:00");
  const { expire } = useQuizTimer();

  useEffect(() => {
    const socket = new WebSocket("ws://localhost:8080/ws/countdown");

    socket.onmessage = (event) => {
      const remainingSeconds = Number(event.data);

      if (remainingSeconds <= 0) {
        expire(); // 🔥 SIGNAL EXPIRY
        socket.close();
        return;
      }

      setTime(formatSeconds(remainingSeconds));
    };

    return () => socket.close();
  }, [expire]);

  return (
    <div className="p-4 bg-azure-mist text-black font-mono text-2xl rounded">
      {time}
    </div>
  );
}
