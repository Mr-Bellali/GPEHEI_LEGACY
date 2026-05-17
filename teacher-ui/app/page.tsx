'use client'

import AuthTemplate from "@/components/layouts/AuthTemplate";
import { useAuthGuard } from "@/hooks/useAuthGuard";

export default function Home() {
  const { checking } = useAuthGuard({ requireAuth: false, redirectTo: '/home' })

  if (checking) return null

  return (
    <>
      <AuthTemplate />
    </>
  );
}
