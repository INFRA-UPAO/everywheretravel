import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CognitoAuthService } from '../../../core/service/cognito/cognito-auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [
    CommonModule
  ]
})
export class LoginComponent implements OnInit {
  isLoading = false;

  constructor(
    private cognitoAuthService: CognitoAuthService
  ) {}

  ngOnInit(): void {
    // No auto-redirect on init; user clicks the login button
  }

  onLogin(): void {
    this.isLoading = true;
    this.cognitoAuthService.login();
  }
}
