"use client"
import { useState } from "react";

interface LoginFormProps {
  onSubmit?: (data: { email: string; password: string; remember: boolean }) => void;
  loading?: boolean;
}

export default function LoginForm({ onSubmit, loading = false }: LoginFormProps) {
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!loading) {
      onSubmit?.({ email, password, remember: rememberMe });
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="w-full h-full flex flex-col justify-between"
    >
      {/* Title & fields container */}
      <div className="flex flex-col gap-[50px]">
        <h1 className="text-[64px] text-white">Login</h1>

        {/* Fields */}
        <div className="flex flex-col gap-6">
          <div className="flex gap-6">
            {/* Email */}
            <div className="flex-1 flex flex-col gap-1">
              <label className="text-white/90 text-sm">Email</label>
              <div className="relative flex items-center">
                <input
                  type="email"
                  name="email"
                  placeholder="xyz@example.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  disabled={loading}
                  className="w-full bg-transparent border-b border-white/50 py-2 pr-7 text-sm text-white/70 placeholder-white/50 outline-none focus:border-white disabled:opacity-50 disabled:cursor-not-allowed"
                />
              </div>
            </div>

            {/* Password */}
            <div className="flex-1 flex flex-col gap-1">
              <label className="text-white/90 text-sm">Password</label>
              <div className="relative flex items-center">
                <input
                  type={showPassword ? "text" : "password"}
                  name="password"
                  placeholder="Your password is here"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  disabled={loading}
                  className="w-full bg-transparent border-b border-white/50 py-2 pr-7 text-sm text-white/70 placeholder-white/50 outline-none focus:border-white disabled:opacity-50 disabled:cursor-not-allowed"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((prev) => !prev)}
                  disabled={loading}
                  className="absolute right-0 text-white/70 hover:text-white flex items-center disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="w-5 h-5"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                    strokeWidth={1.5}
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M2.458 12C3.732 7.943 7.523 5 12 5c4.477 0 8.268 2.943 9.542 7-1.274 4.057-5.065 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                    />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                </button>
              </div>
            </div>
          </div>

          {/* Remember me */}
          {/* <label className="flex items-center gap-1 cursor-pointer select-none">
            <input
              type="checkbox"
              checked={rememberMe}
              onChange={(e) => setRememberMe(e.target.checked)}
              className="hidden"
            />
            <div className="relative w-5 h-5 text-white/80">
              {rememberMe ? (
                <i className="fa-solid fa-square-check absolute inset-0" />
              ) : (
                <i className="fa-regular fa-square absolute inset-0" />
              )}
            </div>
            <span className="text-white/90 text-sm">Remember me</span>
          </label> */}
        </div>
      </div>

      {/* Actions */}
      <div className="flex flex-row items-end justify-between">
        <button
          type="button"
          className="text-sm text-white/50 hover:text-white cursor-pointer"
        >
          Forgot password?
        </button>

        <button
          type="submit"
          disabled={loading}
          className="text-3xl text-[#3D348B] bg-white cursor-pointer px-[34px] py-[10px] hover:bg-[#D1D1D1] rounded-[5px] disabled:opacity-60 disabled:cursor-not-allowed disabled:hover:bg-white"
        >
          {loading ? "Signing in..." : "Sign in"}
        </button>
      </div>
    </form>
  );
}