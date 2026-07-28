package com.gwana.server.common.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    DEFAULT_ERROR("1000", "알 수 없는 에러가 발생했습니다."),
    PASSWORD_ENCRYPTION_FAILED("1001", "비밀번호 암호화 중 에러가 발생했습니다."),
    USER_ALREADY_EXIST("2000", "사용자가 이미 존재합니다."),
    USER_DOES_NOT_EXIST("2001", "사용자가 존재하지 않습니다."),
    USER_CREATE_FAILED("2002", "사용자 생성에 실패하였습니다."),
    KAKAO_EMAIL_REQUIRED("2003", "카카오 이메일 제공에 동의해야 로그인할 수 있습니다."),
    EMAIL_ALREADY_REGISTERED("2004", "이미 가입된 이메일입니다. 기존 계정으로 로그인해주세요."),
    INQUIRY_CREATE_FAILED("3000", "문의 등록에 실패하였습니다."),
    REVIEW_CREATE_FAILED("3001", "리뷰 등록에 실패하였습니다."),
    REVIEW_STAT_UPSERT_FAILED("3002", "리뷰 통계 갱신에 실패하였습니다."),
    AUTHENTICATION_FAILED("4001", "인증에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요."),
    FORBIDDEN_ERROR("4002", "해당 요청에 대한 권한이 없습니다"),
    UNAUTHORIZED_ERROR("4003", "인증이 유효하지 않습니다. 로그인해주세요."),
    INVALID_TOKEN("4004", "유효한 토큰이 아닙니다. 로그인해주세요."),
    EXPIRED_TOKEN_ERROR("4005", "로그인이 만료되었습니다. 다시 로그인해주세요."),
    ACCESS_DENIED("3000", "해당 기능에 접근이 제한됩니다."),
    NOT_MATCHED_AMOUNT("5001", "결제 금액이 일치하지 않습니다."),
    FILE_EMPTY("6001", "파일이 비어있습니다."),
    FILE_SIZE_EXCEEDED("6002", "파일 크기가 허용 용량을 초과했습니다."),
    FILE_VALIDATION_ERROR("6003", "파일 검증 중 오류가 발생했습니다."),
    NOT_ALLOWED_FILE_TYPE("6004", "허용되지 않는 파일 형식입니다. (jpg, png, gif, webp만 허용)"),
    FILE_UPLOAD_FAILED("6005", "파일 업로드에 실패했습니다."),
    S3_SERVER_ERROR("6006", "파일 서버에 오류가 발생했습니다."),
    S3_CONNECTION_ERROR("6007", "파일 서버 연결에 실패했습니다."),
    FILE_COUNT_EXCEEDED("6008", "업로드 허용 개수를 초과했습니다."),
    INVALID_FOLDER_PATH("6009", "허용되지 않는 업로드 경로입니다."),
    INVALID_IMAGE_REFERENCE("6010", "유효하지 않은 이미지 참조입니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return this.code;
    }
}
