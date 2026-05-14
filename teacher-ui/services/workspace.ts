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
  return res.data;
}

export async function getGroupsForModule(moduleId: number) {
  const res = await api.get(`/workspace/groups/${moduleId}`);
  return res.data;
}

export async function getFlow(moduleId: number, groupId: number) {
  const res = await api.get(`/workspace/flow/${moduleId}/${groupId}`);
  return res.data;
}

export async function getAssignments(moduleId: number, groupId: number) {
  const res = await api.get(`/workspace/assignments/${moduleId}/${groupId}`);
  return res.data;
}

export async function createPost(data: { title: string; content: string; module_id: number; groupe_id: number }) {
  const res = await api.post('/workspace/post', data);
  return res.data;
}

export async function createCourse(data: { title: string; content: string; module_id: number }) {
  const res = await api.post('/workspace/course', data);
  return res.data;
}

export async function createHomework(data: { title: string; content: string; deadline: string; module_id: number; groupe_id: number }) {
  const res = await api.post('/workspace/homework', data);
  return res.data;
}