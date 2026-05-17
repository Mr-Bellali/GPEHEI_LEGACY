'use client'

import AuthTemplate from '@/components/layouts/AuthTemplate'
import { useAuthGuard } from '@/hooks/useAuthGuard'

const Loginpage = () => {
  const { checking } = useAuthGuard({ requireAuth: false, redirectTo: '/home' })

  if (checking) return null

  return (
    <>
    <AuthTemplate />
    </>
  )
}

export default Loginpage
