
# Kombiniert die Anzahl der Kombinationen und fuehrt sie in einem neuen Graph zusammen
def merge(a, b, offset_a, offset_b, max):
    c = [0 for i in range(max+1)]
    i = offset_a
    while i <= max:
        j = offset_b
        while i+j <= max:
            c[i+j] += a[i] * b[j]
            j += 1
        i += 1
    return c

# Berechnet mithilfe des Teile-und-Beherrsche Prinzips die Anzahl der moeglichen Kombinationen
def compute(bottles, volume, total_bottles):
    n = len(volume)
    if n > 2:
        middle = int(n/2)
        part_a = volume[middle:]
        part_b = volume[:middle]

        offset_a = max(bottles - sum(part_b), 0)
        offset_b = max(bottles - sum(part_a), 0)

        bottles_a = compute(offset_a, part_a, total_bottles)
        bottles_b = compute(offset_b, part_b, total_bottles)

        return merge(bottles_a, bottles_b, offset_a, offset_b, total_bottles)
    elif n == 2:
        a, b = volume[0], volume[1]

        offset_a = max(bottles - b, 0)
        offset_b = max(bottles - a, 0)

        bottles_a = [1 if i <= a else 0 for i in range(total_bottles+1)]
        bottles_b = [1 if i <= b else 0 for i in range(total_bottles+1)]

        return merge(bottles_a, bottles_b, offset_a, offset_b, total_bottles)
    else:
        return [1 if i <= volume[0] else 0 for i in range(total_bottles+1)]

n = int(input()) # Erste Zeile (N)
k = int(input()) # Zweite Zeile (k)
V = [int(i) for i in input().split()] # Dritte Zeile

print(compute(n, V, n)[n])
