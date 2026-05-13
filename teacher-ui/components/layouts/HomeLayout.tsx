'use client'

import React, { useState, useRef, useEffect } from 'react';
import { House, Pencil, MessageSquare, Library as LibraryIcon, ChevronDown, Bell, Moon, LogOut, Search } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { clearToken } from '@/utils/authUtils';

import Home from '@/components/pages/Home';
import Workspace from '@/components/pages/Workspace';
import Chat from '@/components/pages/Chat';
import LibraryPage from '@/components/pages/Library';

type PageType = 'home' | 'workspace' | 'chat' | 'library';

interface NavItem {
  page: PageType;
  icon: React.ReactNode;
  label: string;
  component: React.ReactNode;
}

export default function HomeLayout() {
  const router = useRouter();
  const [currentPage, setCurrentPage] = useState<PageType>('home');
  const [showLanguageDropdown, setShowLanguageDropdown] = useState(false);
  const [highlightStyle, setHighlightStyle] = useState({ left: 0, width: 0 });
  const navPillRef = useRef<HTMLDivElement>(null);

  const navItems: NavItem[] = [
    { page: 'home',      icon: <House className="w-4 h-4" />,         label: 'Home',      component: <Home /> },
    { page: 'workspace', icon: <Pencil className="w-4 h-4" />,        label: 'Workspace', component: <Workspace /> },
    { page: 'chat',      icon: <MessageSquare className="w-4 h-4" />, label: 'Chat',      component: <Chat /> },
    { page: 'library',   icon: <LibraryIcon className="w-4 h-4" />,   label: 'Library',   component: <LibraryPage /> },
  ];

  const moveHighlight = (element: HTMLElement) => {
    setHighlightStyle({ left: element.offsetLeft, width: element.offsetWidth });
  };

  useEffect(() => {
    const firstItem = navPillRef.current?.querySelector('[data-nav]') as HTMLElement;
    if (firstItem) moveHighlight(firstItem);
  }, []);

  const handleNavClick = (e: React.MouseEvent<HTMLDivElement>, page: PageType) => {
    moveHighlight(e.currentTarget);
    setCurrentPage(page);
  };

  const handleLogout = () => {
    clearToken();
    router.push('/');
  };

  const activePage = navItems.find(item => item.page === currentPage);

  return (
    <div className="w-full h-screen bg-[#F0F0F0] px-6 overflow-hidden">
      {/* Header */}
      <div className="w-full h-[50px] flex justify-between fixed top-[14px] left-0 px-6 z-50">

        {/* Logo & Search */}
        <div className="h-full flex gap-4">
          <div className="w-[50px] h-[50px] bg-white rounded-full flex items-center justify-center">
            <span className="text-[#3D348B] font-bold">EHEI</span>
          </div>
          <div className="flex items-center bg-white rounded-full px-5 w-60 h-[50px] gap-2.5">
            <Search className="w-[18px] h-[18px] text-[#404359]/50 shrink-0" />
            <input
              type="text"
              placeholder="Search for anything"
              className="bg-transparent border-none outline-none text-sm text-black placeholder-[#404359]/50 w-full"
            />
          </div>
        </div>

        {/* Nav pill */}
        <div className="fixed top-[14px] left-1/2 -translate-x-1/2 z-50">
          <div
            ref={navPillRef}
            className="bg-[#3D348B] rounded-full p-1.5 inline-flex items-center gap-0.5 relative shadow-xl"
          >
            <div
              className="absolute top-1.5 h-[calc(100%-12px)] bg-[#7678ED] rounded-full shadow transition-all duration-300 ease-[cubic-bezier(0.4,0,0.2,1)]"
              style={{ left: `${highlightStyle.left}px`, width: `${highlightStyle.width}px` }}
            />
            {navItems.map((item) => (
              <div
                key={item.page}
                data-nav
                onClick={(e) => handleNavClick(e, item.page)}
                className="relative z-10 flex items-center gap-1.5 px-4 py-2 rounded-full cursor-pointer text-white text-sm font-medium whitespace-nowrap select-none"
              >
                {item.icon}
                <span>{item.label}</span>
              </div>
            ))}
          </div>
        </div>

        {/* Actions */}
        <div className="bg-white p-3 h-full flex items-center gap-3 rounded-full">
          <div className="relative">
            <button
              onClick={() => setShowLanguageDropdown(!showLanguageDropdown)}
              className="flex items-center gap-1 cursor-pointer select-none"
            >
              <span className="text-sm font-medium text-[#404359]">EN</span>
              <ChevronDown className="w-4 h-4 text-[#404359]/60 stroke-[2.5]" />
            </button>
            {showLanguageDropdown && (
              <div className="absolute top-full left-0 mt-2 bg-white rounded-2xl shadow-lg py-1 min-w-[80px] z-50">
                {['EN', 'FR', 'AR'].map(lang => (
                  <button
                    key={lang}
                    className="block w-full text-left px-4 py-2 text-sm text-[#404359] hover:bg-gray-50"
                  >
                    {lang}
                  </button>
                ))}
              </div>
            )}
          </div>
          <button><Bell className="w-5 h-5 text-black stroke-[1.75]" /></button>
          <button><Moon className="w-5 h-5 text-black stroke-[1.75] scale-x-[-1]" /></button>
          <button onClick={handleLogout} className="text-red-500 hover:text-red-600">
            <LogOut className="w-5 h-5" />
          </button>
        </div>
      </div>

      {/* Page content */}
      <main className="pt-[78px] h-full overflow-y-auto">
        {activePage?.component}
      </main>
    </div>
  );
}