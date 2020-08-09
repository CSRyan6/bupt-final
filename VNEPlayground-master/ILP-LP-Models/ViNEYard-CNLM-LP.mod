set N;
/* substrate nodes*/

set M;
/* meta nodes */

set NM := N union M;
/* substrate and meta nodes */

set F;
/* virtual links */

param p {i in NM};
/* cpu of node i */

param b {u in NM ,v in NM};
/* bandwidth of edge (u,v) */

param fs{i in F};
/* flow start points */

param fe{i in F};
/* flow end points */

param fd{i in F};
/* flow demands */

var flow{i in F, u in NM, v in NM} >= 0;
/* flow variable (16)*/

var lambda{u in NM, v in NM} >= 0, <=1;
/* indicator variable (17)*/

minimize cost: (sum{u in N, v in N} ((1 / (b[u, v] + 1E-4)) * sum{i in F} flow[i, u, v]))
              + (sum{w in N} ((1 / (p[w] + 1E-4)) * sum{m in M} lambda[m, w] * p[m]));
/* minimum cost multi-commodity flow(7) */

s.t. capcon{u in NM, v in NM}:
sum{i in F} (flow[i, u, v] + flow[i, v, u]) <= b[u,v];
/* capacity constraint (8)*/

s.t. cpucon{m in M, w in N}: p[w] >= lambda[m, w] * p[m];
/* cpu constraint(9) */

s.t. flocon{i in F, u in NM diff {fs[i], fe[i]}}: sum{w in NM} flow[i, u, w] - sum{w in NM} flow[i, w, u] = 0;
/* flow conservation (10)*/

s.t. demsat1{i in F}: sum{w in NM} flow[i, fs[i], w] - sum{w in NM} flow[i, w, fs[i]] = fd[i];
s.t. demsat2{i in F}: sum{w in NM} flow[i, fe[i], w] - sum{w in NM} flow[i, w, fe[i]] = -fd[i];
/* demand satisfaction (11)&(12)*/


s.t. metcon1{u in NM, v in NM}: sum{i in F} flow[i, u, v] <= b[u, v] * lambda[u, v];
s.t. metcon2{u in NM, v in NM}: sum{i in F} flow[i, u, v] <= b[u, v] * lambda[v, u];
s.t. metcon3{m in M}: sum{w in N} lambda[m, w] = 1;
s.t. metcon4{w in N}: sum{m in M} lambda[m, w] <= 1;
s.t. metcon5{u in NM, v in NM}: lambda[u, v] <= b[u,v];

s.t. bincon{u in NM, v in NM}: lambda[u, v] = lambda[v, u];
/* binary constraint */

end;

