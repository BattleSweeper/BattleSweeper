package dev.battlesweeper.objects;

public record UserInfo(Long id, String name) {

    @Override
    public String toString() {
        return "User(" + id + ":" + name + "))";
    }
}
