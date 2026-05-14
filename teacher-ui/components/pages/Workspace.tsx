'use client'

import React, { useState, useEffect } from 'react';
import { ChevronDown, Users, Inbox, AlertCircle, Loader2 } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { getAllModules } from '@/services/workspace';

interface Group {
    id: number;
    group_name: string;
    filiere_name: string;
    student_count: number;
}

interface LevelData {
    name: string;
    total_groups: number;
    groups: Group[];
}

interface Filiere {
    id: number;
    name_filier: string;
}

export default function Workspace() {
    const router = useRouter();
    const [loading, setLoading] = useState(true);
    const [filiereLoading, setFiliereLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [filieres, setFilieres] = useState<Filiere[]>([]);
    const [selectedFiliere, setSelectedFiliere] = useState<Filiere | null>(null);
    const [groupsByLevel, setGroupsByLevel] = useState<Record<number, LevelData>>({});
    const [showDropdown, setShowDropdown] = useState(false);

    // Initial load
    useEffect(() => {
        fetchWorkspace();
    }, []);

    // Close dropdown on outside click
    useEffect(() => {
        const handler = (e: MouseEvent) => {
            const target = e.target as HTMLElement;
            if (!target.closest('[data-dropdown]')) setShowDropdown(false);
        };
        document.addEventListener('click', handler);
        return () => document.removeEventListener('click', handler);
    }, []);

    const fetchWorkspace = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await getAllModules();
            setFilieres(data.filieres);
            setGroupsByLevel(data.groupsByLevel);
            if (data.filieres.length > 0) setSelectedFiliere(data.filieres[0]);
        } catch (err) {
            setError('Failed to load workspace. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    // const handleFiliereSelect = (filiere: Filiere) => {
    //     setSelectedFiliere(filiere);
    //     setShowDropdown(false);
    //     fetchWorkspace(filiere.id);
    // };

    if (loading) {
        return (
            <div className="flex items-center justify-center py-24">
                <Loader2 className="w-8 h-8 animate-spin text-[#3D348B]" />
            </div>
        );
    }

    if (error) {
        return (
            <div className="flex flex-col items-center justify-center py-24 gap-3">
                <AlertCircle className="w-16 h-16 text-red-400" />
                <h3 className="text-lg font-medium text-gray-900">Something went wrong</h3>
                <p className="text-gray-500">{error}</p>
                <button
                    onClick={() => fetchWorkspace()}
                    className="mt-2 px-4 py-2 bg-[#3D348B] text-white rounded-full text-sm hover:bg-[#2d2570]"
                >
                    Retry
                </button>
            </div>
        );
    }

    const hasGroups = Object.values(groupsByLevel).some(l => l.total_groups > 0);

    return (
        <div className="mx-auto flex flex-col gap-6">

            {/* Filiere dropdown */}
            <div className="w-full h-fit flex">
                <div className="relative" data-dropdown>
                    <button
                        onClick={() => setShowDropdown(prev => !prev)}
                        className="flex items-center gap-1 cursor-pointer select-none h-[50px] bg-white px-4 rounded-full"
                    >
                        <span className="text-sm font-medium text-[#404359]">
                            {selectedFiliere?.name_filier ?? 'Select Filiere'}
                        </span>
                        <ChevronDown className="w-4 h-4 text-[#404359]/60 stroke-[2.5]" />
                    </button>

                    {showDropdown && (
                        <div className="absolute top-full left-0 mt-2 bg-white rounded-2xl shadow-lg py-1 min-w-[200px] z-50">
                            {filieres.length > 0 ? (
                                filieres.map(filiere => (
                                    <button
                                        key={filiere.id}
                                        // onClick={() => handleFiliereSelect(filiere)}
                                        className="block w-full text-left px-4 py-2 text-sm text-[#404359] hover:bg-gray-50 rounded-full"
                                    >
                                        {filiere.name_filier}
                                    </button>
                                ))
                            ) : (
                                <span className="block px-4 py-2 text-sm text-gray-500">No filieres assigned</span>
                            )}
                        </div>
                    )}
                </div>
            </div>

            {/* Groups content */}
            <div className="flex flex-col gap-2">
                {filiereLoading ? (
                    <div className="flex items-center justify-center py-12">
                        <Loader2 className="w-8 h-8 animate-spin text-[#3D348B]" />
                    </div>
                ) : hasGroups ? (
                    [1, 2, 3, 4, 5].map(level => {
                        const levelData = groupsByLevel[level];
                        if (!levelData || levelData.total_groups === 0) return null;

                        return (
                            <div key={level} className="mb-6">
                                {/* Level title */}
                                <div className="w-full flex flex-col gap-1 mb-2">
                                    <h1 className="font-bold text-xl text-[#3D348B]">{levelData.name}</h1>
                                    <hr className="border-[#3D348B]" />
                                </div>

                                {/* Groups grid */}
                                <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
                                    {levelData.groups.map(group => (
                                        <div
                                            key={group.id}
                                            onClick={() => router.push(`/group/${group.id}`)}
                                            className="flex flex-col px-4 py-3 bg-[#7678ED] hover:bg-[#3D348B] rounded-[10px] h-[120px] justify-between transition-colors duration-200 cursor-pointer"
                                        >
                                            <h2 className="text-xl text-white font-semibold">
                                                {group.group_name}
                                            </h2>
                                            <div className="flex items-center justify-between text-white text-xs">
                                                <p>{group.filiere_name}</p>
                                                <p className="flex items-center gap-1">
                                                    <Users className="w-4 h-4" />
                                                    {group.student_count}
                                                </p>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>
                        );
                    })
                ) : (
                    <div className="flex flex-col items-center justify-center py-12 gap-3">
                        <Inbox className="w-16 h-16 text-gray-400" />
                        <h3 className="text-lg font-medium text-gray-900">No groups found</h3>
                        <p className="text-gray-500">No groups available for this filiere.</p>
                    </div>
                )}
            </div>
        </div>
    );
}