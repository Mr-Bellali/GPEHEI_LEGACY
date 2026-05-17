'use client'

import { useEffect, useState } from 'react'
import { usePathname, useRouter } from 'next/navigation'
import { clearToken, getToken, isTokenExpired } from '@/utils/authUtils'

type UseAuthGuardOptions = {
  requireAuth: boolean
  redirectTo: string
}

export function useAuthGuard(options: UseAuthGuardOptions) {
  const router = useRouter()
  const pathname = usePathname()
  const [checking, setChecking] = useState(true)
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  useEffect(() => {
    const token = getToken()
    const authed = !!token && !isTokenExpired(token)

    if (!authed) {
      clearToken()
    }

    setIsAuthenticated(authed)

    if (options.requireAuth && !authed) {
      if (pathname !== options.redirectTo) router.replace(options.redirectTo)
      setChecking(false)
      return
    }

    if (!options.requireAuth && authed) {
      if (pathname !== options.redirectTo) router.replace(options.redirectTo)
      setChecking(false)
      return
    }

    setChecking(false)
  }, [options.redirectTo, options.requireAuth, pathname, router])

  return { checking, isAuthenticated }
}
