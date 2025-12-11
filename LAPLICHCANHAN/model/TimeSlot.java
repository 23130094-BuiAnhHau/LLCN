package LAPLICHCANHAN.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Objects;

public class TimeSlot {
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean available = true;
    //Tạo TimeSlot mới
    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("start and end must not be null");
        }
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("end must be after start");
        }
        this.start = start;
        this.end = end;
    }
    // Dùng cho sinh lịch để tránh phá hngr dữ liệu
    public TimeSlot(TimeSlot other) {
        if (other == null) throw new IllegalArgumentException("other must not be null");
        this.start = other.start;
        this.end = other.end;
    }

    // getters / setters
    public LocalDateTime getStart() {
         return start; 
        }
    public LocalDateTime getEnd() {
         return end; 
        }
    public boolean isAvailable() {
         return available;
         }
    public void setAvailable(boolean available) { 
        this.available = available; 
    }
    //Độ dài của slot tính bằng phút 
    public long lengthMinutes() {
        return Duration.between(start, end).toMinutes();
    }
    //Kiểm tra xem 2 TimeSlot có overlap thật sự hay không 
    //Nếu one.end == other.start thì coi là không overlap
    public boolean overlaps(TimeSlot other) {
        if (other == null) return false;

        // this.start < other.end
        boolean a = this.start.isBefore(other.end);
        // other.start < this.end
        boolean b = other.start.isBefore(this.end);

        return a && b;
    }
    //kiểm tra xem một TimeSlot có nằm gọn hoàn toàn trong một TimeSlot khác hay không
     public boolean contains(TimeSlot other) {
        if (other == null) return false;
        boolean startOk = !this.start.isAfter(other.start); // this.start <= other.start
        boolean endOk = !this.end.isBefore(other.end);     // this.end >= other.end
        return startOk && endOk;
    }
    //Tạo bảng sao độc lập của TimeSlots
    public TimeSlot deepCopy() {
        return new TimeSlot(this.start, this.end);
    }

    @Override
    public String toString() {
        return "TimeSlot{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
    /*Hai TimeSlot được xem là bằng nhau nếu chúng có cùng thời điểm
    * start và cùng thời điểm end
    **/
     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeSlot timeSlot = (TimeSlot) o;
        return Objects.equals(start, timeSlot.start) &&
               Objects.equals(end, timeSlot.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
    
