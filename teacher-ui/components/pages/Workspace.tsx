'use client'

import React, { useState, useEffect } from 'react';
import { ChevronDown, Users, Inbox, AlertCircle, Loader2, ChevronLeft, FileText } from 'lucide-react';
import { useRouter } from 'next/navigation';
import WorkspaceDetail from './WorkspaceDetail';
import { getAllModules, getGroupsForModule } from '@/services/workspace';

interface Module {
    module_id: number;
    module_name: string;
    semester: number;
    name_filier: string;
    filiere_id: number;
}

interface Group {
    id: number;
    group_name: string;
    filiere_name: string;
}

export default function Workspace() {
    const router = useRouter();
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [modules, setModules] = useState<Module[]>([]);
    const [selectedModule, setSelectedModule] = useState<Module | null>(null);
    const [groups, setGroups] = useState<Group[]>([]);
    const [selectedGroup, setSelectedGroup] = useState<Group | null>(null);
    const [loadingGroups, setLoadingGroups] = useState(false);

    useEffect(() => {
        fetchModules();
    }, []);

    const fetchModules = async () => {
        setLoading(true);
        setError(null);
        try {
            const data = await getAllModules();
            setModules(data);
        } catch (err) {
            setError('Failed to load modules. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleModuleClick = async (module: Module) => {
        setSelectedModule(module);
        setLoadingGroups(true);
        try {
            const data = await getGroupsForModule(module.module_id);
            setGroups(data);
            if (data.length === 1) {
                setSelectedGroup(data[0]);
            }
        } catch (err) {
            console.error('Failed to load groups:', err);
        } finally {
            setLoadingGroups(false);
        }
    };

    if (selectedModule && selectedGroup) {
        return (
            <WorkspaceDetail 
                module={selectedModule} 
                group={selectedGroup} 
                onBack={() => {
                    setSelectedGroup(null);
                    setSelectedModule(null);
                }} 
            />
        );
    }

    if (loading) {
        return (
            <div className="flex items-center justify-center py-24">
                <Loader2 className="w-8 h-8 animate-spin text-[#3D348B]" />
            </div>
        );
    }

    return (
        <div className="mx-auto flex flex-col gap-6 mb-5">
            {selectedModule ? (
                <div className="flex flex-col gap-6">
                    <div className="flex items-center gap-4">
                        <button 
                            onClick={() => setSelectedModule(null)}
                            className="p-2 hover:bg-gray-200 rounded-full transition-colors"
                        >
                            <ChevronLeft className="w-6 h-6 text-[#3D348B]" />
                        </button>
                        <h1 className="text-2xl font-bold text-[#3D348B]">Select Group for {selectedModule.module_name}</h1>
                    </div>
                    
                    {loadingGroups ? (
                        <div className="flex justify-center py-12">
                            <Loader2 className="w-8 h-8 animate-spin text-[#3D348B]" />
                        </div>
                    ) : (
                        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
                            {groups.map(group => (
                                <div
                                    key={group.id}
                                    onClick={() => setSelectedGroup(group)}
                                    className="flex flex-col px-6 py-4 bg-[#7678ED] hover:bg-[#3D348B] rounded-2xl h-[140px] justify-between transition-all duration-300 cursor-pointer border border-white/10"
                                >
                                    <h2 className="text-2xl text-white font-bold">
                                        Group {group.group_name}
                                    </h2>
                                    <div className="flex items-center justify-between text-white/90 text-sm">
                                        <p>{group.filiere_name}</p>
                                        <div className="flex items-center gap-1">
                                            <Users className="w-4 h-4" />
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            ) : (
                <div className="flex flex-col gap-8">

                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                        {modules.map(module => (
                            <div
                                key={module.module_id}
                                onClick={() => handleModuleClick(module)}
                                className="bg-white rounded-3xl p-6 border border-gray-100 transition-all duration-300 cursor-pointer border border-transparent hover:border-[#7678ED]/30 group"
                            >
                                <div className="w-12 h-12 bg-[#7678ED]/10 rounded-2xl flex items-center justify-center mb-4 group-hover:bg-[#7678ED] transition-colors">
                                    <FileText className="w-6 h-6 text-[#7678ED] group-hover:text-white transition-colors" />
                                </div>
                                <h3 className="text-xl font-bold text-[#3D348B] mb-1">{module.module_name}</h3>
                                <p className="text-sm text-gray-500 mb-4">{module.name_filier}</p>
                                <div className="flex items-center justify-between pt-4 border-t border-gray-100">
                                    <span className="text-xs font-semibold text-[#7678ED] bg-[#7678ED]/10 px-3 py-1 rounded-full">
                                        Semester {module.semester}
                                    </span>
                                    <ChevronDown className="w-5 h-5 text-gray-300 -rotate-90" />
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
}