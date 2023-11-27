#! /usr/bin/env bash
#
#SBATCH --job-name=training_readability_classifier
#SBATCH --mem=64G
#SBATCH --cpus-per-task=8
#SBATCH --time=02:00:00  # two hours
#SBATCH --mail-user=krodinger@fim.uni-passau.de
#SBATCH --constraint=thor
#SBATCH --partition=anywhere
#SBATCH --mem-bind=local
#SBATCH --nodes=1-1
#SBATCH --ntasks=1
#SBATCH --ntasks-per-core=1
#SBATCH --mail-type=FAIL,END
#
# A container image can be built locally or on a SLURM workstation node and
# then be exported to an archive using:
#
# podman save TODO-image-tag > IMAGE.tar
#
# After the archive was copied to the /scratch/$USER/... directory this script
# can be used to run SLURM jobs in the container.
# Make sure this script is located somewhere in `/scratch/$USER` and executable.
# Then start the job with `sbatch ./this-script.sh`.

set -eu

unset XDG_RUNTIME_DIR XDG_CONFIG_HOME
export HOME=/local/$USER/podman.home
export TMPDIR=/local/$USER/tmp

mkdir -p "/local/${USER}/podman.home/"

echo "Loading image..."
podman \
    --root="/local/$USER/podman" \
    load \
    -i "/scratch/$USER/rdh1.tar"

podman \
    --root="/local/$USER/podman" \
    image ls

podman \
    --root="/local/$USER/podman" \
    run \
    --rm \
    -v "$(pwd)/target/readability-decreasing-heuristics-1.0-SNAPSHOT-jar-with-dependencies.jar:/app/target/readability-decreasing-heuristics-1.0-SNAPSHOT-jar-with-dependencies.jar" \
    -v "$(pwd)/res:/app/res" \
    --name "rdh-container" \
    "lukro2011/rdh:1" \
    /bin/bash scripts/run.sh

podman \
    --root="/local/$USER/podman" \
    image prune -a -f
