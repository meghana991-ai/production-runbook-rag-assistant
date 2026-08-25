export interface IngestionResult { documentId: string; documentName: string; pages: number; chunks: number; }
export interface Citation { documentName: string; pageNumber: number; excerpt: string; }
export interface QuestionResponse { answer: string; citations: Citation[]; grounded: boolean; }
