import api from './api';

export interface Module {
  id: number;
  teacher_id: number;
  year_academic_level: number;
  module_name: string;
}

const LEVEL_NAMES: Record<number, string> = {
  1: 'First Year',
  2: 'Second Year',
  3: 'Third Year',
  4: 'Fourth Year',
  5: 'Fifth Year',
};

export async function getAllModules() {
  const res = await api.get('/workspace/modules');
  const modules: Module[] = res.data;

  // Group modules by academic level
  const groupsByLevel: Record<number, { name: string; total_groups: number; groups: any[] }> = {};

  modules.forEach((module) => {
    const level = module.year_academic_level;
    if (!groupsByLevel[level]) {
      groupsByLevel[level] = {
        name: LEVEL_NAMES[level] ?? `Year ${level}`,
        total_groups: 0,
        groups: [],
      };
    }
    groupsByLevel[level].groups.push({
      id: module.id,
      group_name: module.module_name,
      filiere_name: '',
      student_count: 0,
    });
    groupsByLevel[level].total_groups++;
  });

  return { filieres: [], groupsByLevel };
}