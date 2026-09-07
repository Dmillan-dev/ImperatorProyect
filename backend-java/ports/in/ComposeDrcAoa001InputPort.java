package imperator.ports.in;

import imperator.application.composecase.ComposeDrcAoa001Command;
import imperator.application.composecase.ComposeDrcAoa001Result;

public interface ComposeDrcAoa001InputPort {
    ComposeDrcAoa001Result compose(ComposeDrcAoa001Command command);
}
