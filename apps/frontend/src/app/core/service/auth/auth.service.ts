import { inject, Injectable } from '@angular/core';
import { StorageService } from '../storage.service';
import { BehaviorSubject, Observable, map, tap } from 'rxjs';
import { AuthResponse } from '../../../shared/models/auth/auth-response-model';
import { CognitoAuthService } from '../cognito/cognito-auth.service';
import { UserService } from '../User/user.service';
import { buildPermissions } from '../../../shared/models/role.model';

@Injectable({
  providedIn: 'root'
})
export class AuthServiceService {
  private storageService = inject(StorageService);
  private cognitoAuthService = inject(CognitoAuthService);
  private userService = inject(UserService);

  // Observable central para usuario actual
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(this.storageService.getAuthData());
  currentUser$ = this.currentUserSubject.asObservable();

  login(): void {
    this.cognitoAuthService.login();
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

  loadCurrentUserProfile(): Observable<AuthResponse> {
    return this.userService.getCurrentProfile().pipe(
      map(profile => ({
        id: profile.id,
        token: '',
        name: profile.name,
        role: profile.role ?? '',
        permissions: buildPermissions(profile.role)
      })),
      tap(authData => this.updateCurrentUser(authData))
    );
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
