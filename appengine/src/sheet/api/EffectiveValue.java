package sheet.api;
import expression.api.ObjType;

import java.io.Serializable;

public interface EffectiveValue extends Serializable {
    ObjType getObjType();
    Object getValue();
}
