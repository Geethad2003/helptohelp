export interface AuthResponse {
    token: string;
    role: 'HELPER' | 'SEEKER';
    email: string;
    message?: string;
}

export interface OtpResponse {
    message: string;
    success?: boolean;
}

export interface LoginRequest {
    email: string;
    password: string;
}

export interface SignupRequest {
    email: string;
    password: string;
    role: 'HELPER' | 'SEEKER';
}

export interface OtpRequest {
    email: string;
    otp?: string;
}

export interface User {
    email: string;
    role: 'HELPER' | 'SEEKER';
}