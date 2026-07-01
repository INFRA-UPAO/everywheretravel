import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CognitoAuthService } from '../../../core/service/cognito/cognito-auth.service';
import { StorageService } from '../../../core/service/storage.service';
import { UserService } from '../../../core/service/User/user.service';
import { AuthResponse } from '../../../shared/models/auth/auth-response-model';
import { ROLES_DEFINITION, RoleType, Permission } from '../../../shared/models/role.model';

@Component({
  selector: 'app-callback',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="callback-container">
      <div class="callback-content">
        <div class="spinner"></div>
        <p *ngIf="!errorMessage">Procesando autenticacion...</p>
        <p *ngIf="errorMessage" class="error-text">{{ errorMessage }}</p>
      </div>
    </div>
  `,
  styles: [`
    .callback-container {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      background-color: #f5f5f5;
    }
    .callback-content {
      text-align: center;
    }
    .spinner {
      width: 40px;
      height: 40px;
      border: 4px solid #e0e0e0;
      border-top-color: #3b82f6;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
      margin: 0 auto 1rem;
    }
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
    .error-text {
      color: #ef4444;
    }
  `]
})
export class CallbackComponent implements OnInit {
  errorMessage = '';

  private cognitoAuthService = inject(CognitoAuthService);
  private storageService = inject(StorageService);
  private userService = inject(UserService);
  private router = inject(Router);

  ngOnInit(): void {
    this.cognitoAuthService.handleCallback().subscribe({
      next: (isAuthenticated) => {
        if (isAuthenticated) {
          this.fetchUserProfile();
        } else {
          this.errorMessage = 'Error de autenticacion. Redirigiendo al inicio...';
          setTimeout(() => this.router.navigate(['/auth/login']), 3000);
        }
      },
      error: () => {
        this.errorMessage = 'Error procesando la autenticacion. Redirigiendo al inicio...';
        setTimeout(() => this.router.navigate(['/auth/login']), 3000);
      }
    });
  }

  private fetchUserProfile(): void {
    this.userService.getCurrentProfile().subscribe({
      next: (profile) => {
        // The JWT token is managed by the OIDC library, so we store an empty string
        const authData: AuthResponse = {
          id: profile.id,
          token: '',
          name: profile.name,
          role: profile.role ?? '',
          permissions: this.buildPermissions(profile.role)
        };
        this.storageService.setAuthData(authData);
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.errorMessage = 'Error obteniendo perfil de usuario. Redirigiendo al inicio...';
        setTimeout(() => this.router.navigate(['/auth/login']), 3000);
      }
    });
  }

  private buildPermissions(role: string | undefined | null): { [module: string]: Permission[] } {
    const roleDefinition = ROLES_DEFINITION[role as RoleType];
    if (!roleDefinition) {
      return {};
    }

    const permissions: { [module: string]: Permission[] } = {};
    for (const moduleKey of roleDefinition.modules) {
      permissions[moduleKey] = roleDefinition.permissions as Permission[];
    }
    return permissions;
  }
}
