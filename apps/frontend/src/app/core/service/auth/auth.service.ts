import { inject, Injectable } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { StorageService } from '../storage.service';
import { AuthRequest } from '../../../shared/models/auth/auth-request-model';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { AuthResponse } from '../../../shared/models/auth/auth-response-model';
import { CognitoAuthService } from '../cognito/cognito-auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {
  private baseURL = `${environment.baseURL}/auth`;
  private http = inject(HttpClient);
  private storageService = inject(StorageService);
  private cognitoAuthService = inject(CognitoAuthService);

  // Observable central para usuario actual
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.storageService.getAuthData());
  currentUser$ = this.currentUserSubject.asObservable();

  login(authRequest: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseURL}/login`, authRequest).pipe(
      tap(response => {
        this.storageService.setAuthData(response);
        this.currentUserSubject.next(response);
      })
    );
  }

  logout(): void {
    this.storageService.clearAuthData();
    this.currentUserSubject.next(null);
    this.cognitoAuthService.logout();
  }

  isAuthenticated(): boolean {
    return !!this.currentUserSubject.value;
  }

  getUser(): AuthResponse | null {
    return this.currentUserSubject.value;
  }

  getRole(): string | null {
    return this.currentUserSubject.value?.role || null;
  }

  getCurrentUserId(): number | null {
    return this.currentUserSubject.value?.id || null;
  }

  updateCurrentUser(data: AuthResponse): void {
    this.storageService.setAuthData(data);
    this.currentUserSubject.next(data);
  }

  updateCurrentUserName(name: string): void {
    const current = this.currentUserSubject.value;
    if (!current) {
      return;
    }
    const updated = { ...current, name };
    this.storageService.setAuthData(updated);
    this.currentUserSubject.next(updated);
  }

  hasPermission(moduleKey: string, action: 'READ' | 'CREATE' | 'UPDATE' | 'DELETE'): boolean {
    return this.currentUserSubject.value?.permissions?.[moduleKey]?.includes(action) || false;
  }
}
