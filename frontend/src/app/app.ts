import { Component, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { RunbookApiService } from './runbook-api.service';
import { QuestionResponse } from './runbook.models';

@Component({ selector: 'app-root', imports: [], templateUrl: './app.html', styleUrl: './app.css' })
export class App {
  private readonly api = inject(RunbookApiService);
  protected readonly selectedFile = signal<File | null>(null);
  protected readonly question = signal('');
  protected readonly uploading = signal(false);
  protected readonly asking = signal(false);
  protected readonly uploadMessage = signal('');
  protected readonly uploadError = signal('');
  protected readonly askError = signal('');
  protected readonly answer = signal<QuestionResponse | null>(null);

  protected selectFile(event: Event): void {
    this.selectedFile.set((event.target as HTMLInputElement).files?.item(0) ?? null);
    this.uploadMessage.set(''); this.uploadError.set('');
  }
  protected upload(): void {
    const file = this.selectedFile();
    if (!file || this.uploading()) return;
    this.uploading.set(true); this.uploadError.set('');
    this.api.upload(file).pipe(finalize(() => this.uploading.set(false))).subscribe({
      next: result => { this.uploadMessage.set(`${result.documentName} indexed · ${result.pages} pages · ${result.chunks} chunks`); this.selectedFile.set(null); },
      error: error => this.uploadError.set(this.errorMessage(error, 'The runbook could not be indexed.'))
    });
  }
  protected updateQuestion(event: Event): void { this.question.set((event.target as HTMLTextAreaElement).value); }
  protected ask(event: Event): void {
    event.preventDefault();
    const question = this.question().trim();
    if (!question || this.asking()) return;
    this.asking.set(true); this.askError.set(''); this.answer.set(null);
    this.api.ask(question).pipe(finalize(() => this.asking.set(false))).subscribe({
      next: response => this.answer.set(response),
      error: error => this.askError.set(this.errorMessage(error, 'The assistant could not answer right now.'))
    });
  }
  protected formatBytes(bytes: number): string { return bytes < 1_000_000 ? `${Math.ceil(bytes / 1_000)} KB` : `${(bytes / 1_000_000).toFixed(1)} MB`; }
  private errorMessage(error: { error?: { detail?: string } }, fallback: string): string { return error?.error?.detail || fallback; }
}
