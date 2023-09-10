package com.example.leetcodecloneapi.executer;


public class CodeEntity {
    private String code;
    private String type;
    private String no;

    public CodeEntity() {
    }

    public CodeEntity(String code, String type, String no) {
        this.code = code;
        this.type = type;
        this.no = no;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
