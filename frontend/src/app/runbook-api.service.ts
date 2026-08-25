import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { IngestionResult, QuestionResponse } from './runbook.models';

@Injectable({ providedIn: 'root' })
export class RunbookApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1';
  upload(file: File): Observable<IngestionResult> {
    const form = new FormData(); form.append('file', file);
    return this.http.post<IngestionResult>(`${this.baseUrl}/documents`, form);
  }
  ask(question: string): Observable<QuestionResponse> { return this.http.post<QuestionResponse>(`${this.baseUrl}/questions`, { question }); }
}
