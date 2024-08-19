package expression.api;
import sheet.api.EffectiveValue;

import java.io.Serializable;

public interface Expression extends Serializable {
    EffectiveValue eval();
    ObjType type();
}
