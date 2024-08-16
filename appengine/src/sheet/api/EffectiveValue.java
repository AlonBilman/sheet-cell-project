package sheet.api;
import expression.api.ObjType;

public interface EffectiveValue {
    ObjType getObjType();
    Object getValue();
}
