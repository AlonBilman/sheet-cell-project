package http.dto;

public class EffectiveValueDtoResp {
    private Object value;
    private ObjType type;

    public EffectiveValueDtoResp() {
    }

    public ObjType getObjType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}