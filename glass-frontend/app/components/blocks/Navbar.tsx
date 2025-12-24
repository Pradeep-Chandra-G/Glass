import React from "react";
import Logo from "../ui/Logo";
import Banner from "../ui/Banner";
import ServerTimer from "../ui/Timer";

function Navbar() {
  return (
    <div className="flex w-full items-center justify-around mt-8 select-none">
      <Logo />
      <div className="flex items-center w-[64%] bg-azure-mist text-black p-4 rounded-md text-xl">
        <Banner title="Accenture Aptitude Test 2025"/>
      </div>
      <ServerTimer/>
    </div>
  );
}

export default Navbar;
