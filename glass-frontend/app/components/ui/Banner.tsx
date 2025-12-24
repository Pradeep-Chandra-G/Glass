"use client";

import React from "react";

function Banner({ title }: { title?: string }) {
  return (
    <div className="flex w-full items-center justify-around">
      {title || "Banner"}
    </div>
  );
}

export default Banner;
