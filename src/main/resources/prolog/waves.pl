% random_zombie/2(-RandomNumber, +MaxValue)
% Returns a RandomNumber between 1 and MaxValue.
% In this game each number is associated to a zombie.
random_zombie(X, Max) :- rand_int(Max, N), X is N + 1.

% wave/2(+WavePower, -ListOfZombies)
% Returns a wave of zombies of the given power.
wave(0, []):- !.
wave(1, [1]):- !.
wave(2, [X|L]) :- random_zombie(X, 2), M is 2 - X, wave(M, L), !.
wave(N, [X|L]) :- N > 2, random_zombie(X, 3), M is N - X, wave(M, L).