package samt.smajilbasic.model;

public class Cursor {

    private int position = 0;
    private int buffer = Resources.DUPLICATES_BUFFER_LENGTH;

    public Cursor(Integer buffer) {
        if (buffer != null) {
            if (buffer > 0) {
                this.buffer = buffer;
            }
        }
    }

    public int getBuffer() {
        return buffer;
    }

    public int getPosition() {
        return position;
    }

    public int advance() {
        return position += buffer;
    }

    public int revert() {
        if (position - buffer < 0)
            position = 0;
        else
            position -= buffer;
        return position;
    }

    public int resetCursor() {
        return position = 0;
    }
}
