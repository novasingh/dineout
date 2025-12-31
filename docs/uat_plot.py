import csv
import matplotlib.pyplot as plt
import numpy as np

def read_results(path='docs/UAT_RESULTS.csv'):
    rows = []
    with open(path, newline='', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for r in reader:
            rows.append(r)
    return rows

def aggregate(rows):
    qcols = ['Q1','Q2','Q3','Q4','Q5','Q6']
    agg = {q: [0]*5 for q in qcols}
    for r in rows:
        for q in qcols:
            v = int(r[q])
            agg[q][v-1] += 1
    return agg

def plot(agg):
    qcols = list(agg.keys())
    fig, axes = plt.subplots(2,3, figsize=(14,8))
    axes = axes.flatten()
    for i,q in enumerate(qcols):
        axes[i].bar([1,2,3,4,5], agg[q], color='C'+str(i))
        axes[i].set_title(q)
        axes[i].set_xticks([1,2,3,4,5])
        axes[i].set_xlabel('Likert (1-5)')
    plt.tight_layout()
    plt.savefig('docs/uat_summary.png')
    print('Saved docs/uat_summary.png')

if __name__ == '__main__':
    rows = read_results()
    agg = aggregate(rows)
    plot(agg)
