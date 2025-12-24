"use client";

import React from "react";

function StatusBar({
  answered,
  unseen,
  seen,
  review,
}: {
  answered?: number;
  unseen?: number;
  seen?: number;
  review?: number;
}) {
  return (
    <div className="flex flex-row gap-4 items-center justify-around text-xl font-mono">
      <div>🎯{<span className="font-bold">{answered}</span>}</div>
      <div>❓{<span className="font-bold">{unseen}</span>}</div>
      <div>👀{<span className="font-bold">{seen}</span>}</div>
      <div>🕵️‍♂️{<span className="font-bold">{review}</span>}</div>
    </div>
  );
}

export default StatusBar;
